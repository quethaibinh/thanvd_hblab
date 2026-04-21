from fastapi import Depends, FastAPI, HTTPException, Query
from fastapi.middleware.gzip import GZipMiddleware

from dependencies import (
    get_article_query_service,
)
from services import ArticleQueryService


def create_api(logger, crawler_service) -> FastAPI:
    api = FastAPI(title="Pody Article Service", version="1.1.0")
    api.add_middleware(GZipMiddleware, minimum_size=1024)

    async def health_response():
        from datetime import datetime

        return {"status": "ok", "timestamp": datetime.now().isoformat()}

    @api.get("/health")
    async def health():
        return await health_response()

    @api.post("/api/v1/article/crawl")
    async def trigger_crawl():
        try:
            return await crawler_service.crawl_all_sources()
        except Exception as exc:
            logger.error(f"API Error triggering crawl: {str(exc)}")
            raise HTTPException(status_code=500, detail="Internal server error") from exc

    @api.get("/api/v1/article")
    async def list_articles(
        limit: int = Query(50, ge=1, le=100),
        offset: int = Query(0, ge=0),
        category: str | None = Query(None, description="Filter by category"),
        q: str | None = Query(None, description="Search keyword in title/summary"),
        article_query_service: ArticleQueryService = Depends(get_article_query_service),
    ):
        try:
            return await article_query_service.list_articles(
                limit=limit,
                offset=offset,
                category=category,
                query=q,
            )
        except HTTPException:
            raise
        except Exception as exc:
            logger.error(f"API Error fetching articles: {str(exc)}")
            raise HTTPException(status_code=500, detail="Internal server error") from exc

    # Keep static category routes registered before the dynamic article-id route.
    # Starlette/FastAPI path matching is order-sensitive, so `/categories`
    # must be handled here instead of falling through to `/{article_id}`.
    @api.get("/api/v1/article/categories")
    @api.get("/api/v1/article/categories/")
    async def list_categories(
        article_query_service: ArticleQueryService = Depends(get_article_query_service),
    ):
        try:
            return await article_query_service.list_categories()
        except HTTPException:
            raise
        except Exception as exc:
            logger.error(f"API Error fetching article categories: {str(exc)}")
            raise HTTPException(status_code=500, detail="Internal server error") from exc

    @api.get("/api/v1/article/{article_id}")
    async def get_article(
        article_id: int,
        article_query_service: ArticleQueryService = Depends(get_article_query_service),
    ):
        try:
            return await article_query_service.get_article_detail(
                article_id=article_id,
            )
        except HTTPException:
            raise
        except Exception as exc:
            logger.error(f"API Error fetching article {article_id}: {str(exc)}")
            raise HTTPException(status_code=500, detail="Internal server error") from exc

    return api
