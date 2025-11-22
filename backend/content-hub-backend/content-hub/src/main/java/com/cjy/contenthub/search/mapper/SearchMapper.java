package com.cjy.contenthub.search.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchMovieResultsDto;
import com.cjy.contenthub.common.integration.tmdb.dto.TmdbSearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchComicsResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchMovieResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResponseDto;
import com.cjy.contenthub.search.controller.dto.SearchTvResultsDto;
import com.cjy.contenthub.search.controller.dto.SearchVideoResponseDto;

/**
 * 검색 데이터 매핑을 위한 MapStruct 매퍼 인터페이스
 * MapStruct를 사용하여 빌드시 자동으로 구현체가 생성됨
 */
@Mapper(componentModel = "spring")
public interface SearchMapper {
	
	/**
	 * TMDB TV 결과를 Search TV 결과로 매핑
	 * 
	 * @param tmdbTvResults
	 * @return SearchTvResultsDto
	 */
	@Named("tvResultsToTmdbTvResults")
	@Mapping(target = "contentMediaType", ignore = true)
	@Mapping(target = "genreNames", ignore = true)
	@Mapping(target = "isWishlisted", ignore = true)
	SearchTvResultsDto tvResultsToTmdbTvResults(TmdbSearchTvResultsDto tmdbTvResults);
	
	/**
	 * TMDB TV 결과 리스트를 Search TV 결과 리스트로 매핑
	 * 
	 * @param tmdbTvResultsList
	 * @return List<SearchTvResultsDto>
	 */
	@IterableMapping(qualifiedByName = "tvResultsToTmdbTvResults")
	List<SearchTvResultsDto> tvResultsListToTmdbTvResultsList(List<TmdbSearchTvResultsDto> tmdbTvResultsList);
	
	/**
	 * TMDB Movie 결과를 Search Movie 결과로 매핑
	 * 
	 * @param tmdbMovieResults
	 * @return SearchMovieResultsDto
	 */
	@Named("movieResultsToTmdbMovieResults")
	@Mapping(target = "contentMediaType", ignore = true)
	@Mapping(target = "genreNames", ignore = true)
	@Mapping(target = "isWishlisted", ignore = true)
	SearchMovieResultsDto movieResultsToTmdbMovieResults(TmdbSearchMovieResultsDto tmdbMovieResults);
	
	/**
	 * TMDB Movie 결과 리스트를 Search Movie 결과 리스트로 매핑
	 * 
	 * @param tmdbMovieResultsList
	 * @return List<SearchMovieResultsDto>
	 */
	@IterableMapping(qualifiedByName = "movieResultsToTmdbMovieResults")
	List<SearchMovieResultsDto> movieResultsListToTmdbMovieResultsList(List<TmdbSearchMovieResultsDto> tmdbMovieResultsList);
	
	/**
	 * SearchVideoResponseDto 객체의 깊은 복사본을 생성
	 * 
	 * @param source 원본 SearchVideoResponseDto 객체
	 * @return 깊은 복사된 SearchVideoResponseDto 객체
	 */
	SearchVideoResponseDto deepCopyForVideoResponse(SearchVideoResponseDto source);
	
	/**
	 * SearchTvResponseDto 객체의 깊은 복사본을 생성
	 * 
	 * @param source 원본 SearchTvResponseDto 객체
	 * @return 깊은 복사된 SearchTvResponseDto 객체
	 */
	SearchTvResponseDto deepCopyForTvResponse(SearchTvResponseDto source);
	
	/**
	 * SearchMovieResponseDto 객체의 깊은 복사본을 생성
	 * 
	 * @param source 원본 SearchMovieResponseDto 객체
	 * @return 깊은 복사된 SearchMovieResponseDto 객체
	 */
	SearchMovieResponseDto deepCopyForMovieResponse(SearchMovieResponseDto source);
	
	/**
	 * SearchComicsResponseDto 객체의 깊은 복사본을 생성
	 * 
	 * @param source 원본 SearchComicsResponseDto 객체
	 * @return 깊은 복사된 SearchComicsResponseDto 객체
	 */
	SearchComicsResponseDto deepCopyForComicsResponse(SearchComicsResponseDto source);

}
