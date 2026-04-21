"""
Repositories package - Data access layer.
"""
from .article_category_projection_repository import ArticleCategoryProjectionRepository
from .article_query_repository import ArticleQueryRepository
from .article_repository import ArticleRepository
from .article_write_repository import ArticleWriteRepository
from .news_source_repository import NewsSourceRepository

__all__ = [
    "NewsSourceRepository",
    "ArticleRepository",
    "ArticleWriteRepository",
    "ArticleCategoryProjectionRepository",
    "ArticleQueryRepository",
]
