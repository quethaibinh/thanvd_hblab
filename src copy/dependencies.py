from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from config.database import get_db_session
from repositories import ArticleQueryRepository
from services import ArticleQueryService


async def get_session_dependency():
    async with get_db_session() as session:
        yield session


async def get_article_query_service(
    session: AsyncSession = Depends(get_session_dependency),
) -> ArticleQueryService:
    return ArticleQueryService(query_repository=ArticleQueryRepository(session))
