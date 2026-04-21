from fastapi import HTTPException, status

from repositories import ArticleQueryRepository
from schemas import clean_text


class ArticleQueryService:
    """Application service for article listing and detail views."""

    def __init__(
        self,
        query_repository: ArticleQueryRepository,
    ):
        self.query_repository = query_repository

    async def list_articles(self, limit: int, offset: int, category=None, query=None) -> dict:
        results = await self.query_repository.list_articles_with_extra(
            limit=limit,
            offset=offset,
            category=category,
            query=query,
        )
        return {
            "count": len(results),
            "limit": limit,
            "offset": offset,
            "articles": [
                {
                    "id": article.id,
                    "title": clean_text(article.title),
                    "summary": clean_text(article.summary),
                    "author": clean_text(article.author),
                    "category": category_name,
                    "view_count": views,
                    "thumbnail_url": article.thumbnail_url,
                    "original_url": article.original_url,
                    "published_at": article.published_at.isoformat() if article.published_at else None,
                    "source_id": article.source_id,
                    "status": article.status,
                }
                for article, category_name, views in results
            ],
        }

    async def list_categories(self) -> dict:
        categories = await self.query_repository.list_categories_with_counts()
        return {
            "count": len(categories),
            "categories": [
                {
                    "id": category.id,
                    "slug": category.slug,
                    "name": clean_text(category.name),
                    "description": clean_text(category.description),
                    "article_count": article_count,
                }
                for category, article_count in categories
            ],
        }

    async def get_article_detail(self, article_id: int) -> dict:
        detail = await self.query_repository.get_article_detail(article_id)
        if not detail:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Article not found")

        article, categories = detail

        return {
            "id": article.id,
            "title": clean_text(article.title),
            "content": article.content,
            "summary": clean_text(article.summary),
            "author": clean_text(article.author),
            "categories": categories,
            "thumbnail_url": article.thumbnail_url,
            "original_url": article.original_url,
            "published_at": article.published_at.isoformat() if article.published_at else None,
            "source_id": article.source_id,
        }
