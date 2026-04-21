from typing import Optional

from sqlalchemy.ext.asyncio import AsyncSession

from .article_write_repository import ArticleWriteRepository


class ArticleRepository:
    """
    Backward-compatible facade that delegates to focused repositories.
    Keep crawler and older call sites stable while newer code uses smaller repos/services.
    """

    def __init__(self, session: AsyncSession):
        self.session = session
        self.write = ArticleWriteRepository(session)

    async def get_by_id(self, article_id: int):
        return await self.write.get_by_id(article_id)

    async def exists_by_url(self, original_url: str) -> bool:
        return await self.write.exists_by_url(original_url)

    async def insert_article(
        self,
        title: str,
        original_url: str,
        source_id: int,
        content: Optional[str] = None,
        author: Optional[str] = None,
        summary: Optional[str] = None,
        thumbnail_url: Optional[str] = None,
        published_at=None,
        status: str = "PUBLISHED",
    ):
        return await self.write.insert_article(
            title=title,
            original_url=original_url,
            source_id=source_id,
            content=content,
            author=author,
            summary=summary,
            thumbnail_url=thumbnail_url,
            published_at=published_at,
            status=status,
        )

    async def add_category(
        self,
        article_id: int,
        category_name: str,
        description: Optional[str] = None,
        is_primary: Optional[bool] = None,
    ) -> None:
        await self.write.add_category(
            article_id,
            category_name,
            description=description,
            is_primary=is_primary,
        )

__all__ = ["ArticleRepository"]
