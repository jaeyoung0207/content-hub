-- 스키마 생성
CREATE SCHEMA IF NOT EXISTS content AUTHORIZATION myuser;
-- 권한 부여 및 기본 스키마 설정
ALTER ROLE myuser SET search_path = content, public;