import os
import sys
import unittest
from datetime import datetime, timezone
from types import SimpleNamespace

from fastapi.testclient import TestClient

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from api import create_api
from dependencies import get_article_query_service
from services import ArticleQueryService
from utils.logger import get_logger


class FakeArticleRepository:
    def __init__(self):
        self.articles = [
            SimpleNamespace(
                id=1,
                title="Tech article",
                content="<p>Body</p>",
                summary="Summary",
                author="Pody",
                thumbnail_url="https://example.com/image.jpg",
                original_url="https://example.com/article-1",
                published_at=datetime(2026, 3, 20, 10, 0, tzinfo=timezone.utc),
                source_id=9,
                status="PUBLISHED",
                primary_category="Tech",
            ),
            SimpleNamespace(
                id=2,
                title="Business article",
                content="<p>Body</p>",
                summary="Summary",
                author="Pody",
                thumbnail_url="https://example.com/image-2.jpg",
                original_url="https://example.com/article-2",
                published_at=datetime(2026, 3, 21, 10, 0, tzinfo=timezone.utc),
                source_id=10,
                status="PUBLISHED",
                primary_category="Business",
            ),
        ]
        self.categories = [
            SimpleNamespace(
                id="205f73c5-8394-48e1-a0c9-bdbfa8245041",
                slug="tech",
                name="Tech",
                description="Cong nghe va san pham so",
            ),
            SimpleNamespace(
                id="1463d0e3-5dc9-4ea6-a08b-d1916c76bfdf",
                slug="business",
                name="Business",
                description="Kinh doanh",
            ),
        ]

    async def list_articles_with_extra(self, limit=20, offset=0, category=None, query=None):
        results = self.articles
        if category == "tech":
            results = [self.articles[0]]
        elif query == "business":
            results = [self.articles[1]]
        return [
            (article, article.primary_category, 0)
            for article in results[offset : offset + limit]
        ]

    async def get_article_detail(self, article_id: int):
        article = next((item for item in self.articles if item.id == article_id), None)
        if not article:
            return None
        return article, [article.primary_category]

    async def list_categories_with_counts(self):
        return [
            (self.categories[0], 12),
            (self.categories[1], 4),
        ]


class FakeCrawlerService:
    def __init__(self) -> None:
        self.calls = 0

    async def crawl_all_sources(self):
        self.calls += 1
        return {
            "sources": 1,
            "feeds": 2,
            "articles_saved": 3,
            "articles_skipped": 4,
            "errors": 0,
        }


def create_client():
    repo = FakeArticleRepository()
    crawler = FakeCrawlerService()
    app = create_api(get_logger("test-api"), crawler)

    async def override_query_service():
        return ArticleQueryService(query_repository=repo)

    app.dependency_overrides[get_article_query_service] = override_query_service
    return TestClient(app), crawler


class ArticleAPIRouteTests(unittest.TestCase):
    def test_health_returns_ok(self):
        client, _ = create_client()

        response = client.get("/health")

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["status"], "ok")

    def test_list_articles_returns_primary_category_string(self):
        client, _ = create_client()

        response = client.get("/api/v1/article")

        self.assertEqual(response.status_code, 200)
        payload = response.json()
        self.assertEqual(payload["count"], 2)
        self.assertEqual(payload["articles"][0]["category"], "Tech")

    def test_list_articles_keeps_category_filter_ordering_behavior(self):
        client, _ = create_client()

        response = client.get("/api/v1/article", params={"category": "tech"})

        self.assertEqual(response.status_code, 200)
        payload = response.json()
        self.assertEqual(payload["count"], 1)
        self.assertEqual(payload["articles"][0]["category"], "Tech")

    def test_list_articles_keeps_search_behavior(self):
        client, _ = create_client()

        response = client.get("/api/v1/article", params={"q": "business"})

        self.assertEqual(response.status_code, 200)
        payload = response.json()
        self.assertEqual(payload["count"], 1)
        self.assertEqual(payload["articles"][0]["category"], "Business")

    def test_get_article_detail_returns_categories(self):
        client, _ = create_client()

        response = client.get("/api/v1/article/1")

        self.assertEqual(response.status_code, 200)
        payload = response.json()
        self.assertEqual(payload["categories"], ["Tech"])
        self.assertEqual(payload["title"], "Tech article")

    def test_list_categories_returns_real_category_payload(self):
        client, _ = create_client()

        response = client.get("/api/v1/article/categories")

        self.assertEqual(response.status_code, 200)
        payload = response.json()
        self.assertEqual(payload["count"], 2)
        self.assertEqual(payload["categories"][0]["slug"], "tech")
        self.assertEqual(payload["categories"][0]["article_count"], 12)

    def test_list_categories_with_trailing_slash_returns_real_category_payload(self):
        client, _ = create_client()

        response = client.get("/api/v1/article/categories/")

        self.assertEqual(response.status_code, 200)
        payload = response.json()
        self.assertEqual(payload["count"], 2)
        self.assertEqual(payload["categories"][1]["slug"], "business")

    def test_trigger_crawl_returns_crawler_stats(self):
        client, crawler = create_client()

        response = client.post("/api/v1/article/crawl")

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["articles_saved"], 3)
        self.assertEqual(crawler.calls, 1)


if __name__ == "__main__":
    unittest.main()
