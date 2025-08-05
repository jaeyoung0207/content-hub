/* eslint-disable */
/* tslint:disable */
// @ts-nocheck
/*
 * ---------------------------------------------------------------
 * ## THIS FILE WAS GENERATED VIA SWAGGER-TYPESCRIPT-API        ##
 * ##                                                           ##
 * ## AUTHOR: acacode                                           ##
 * ## SOURCE: https://github.com/acacode/swagger-typescript-api ##
 * ---------------------------------------------------------------
 */

import {
  SearchContentComicsResponseDto,
  SearchContentVideoResponseDto,
  TmdbSearchMovieDto,
  TmdbSearchTvDto,
} from "./data-contracts";
import { HttpClient, RequestParams } from "./http-client";

export class SearchContent<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags search-content-controller
   * @name SearchVideo
   * @request GET:/searchContent/searchVideo
   */
  searchVideo = (
    query: {
      query: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<SearchContentVideoResponseDto, any>({
      path: `/searchContent/searchVideo`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-content-controller
   * @name SearchMovie
   * @request GET:/searchContent/searchMovie
   */
  searchMovie = (
    query: {
      query: string;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<TmdbSearchMovieDto, any>({
      path: `/searchContent/searchMovie`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-content-controller
   * @name SearchKeyword
   * @request GET:/searchContent/searchKeyword
   */
  searchKeyword = (
    query: {
      query: string;
    },
    params: RequestParams = {},
  ) =>
    this.request<string[], any>({
      path: `/searchContent/searchKeyword`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-content-controller
   * @name SearchDrama
   * @request GET:/searchContent/searchDrama
   */
  searchDrama = (
    query: {
      query: string;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<TmdbSearchTvDto, any>({
      path: `/searchContent/searchDrama`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-content-controller
   * @name SearchComics
   * @request GET:/searchContent/searchComics
   */
  searchComics = (
    query: {
      query: string;
      /** @format int32 */
      page?: number;
      isMainPage: boolean;
    },
    params: RequestParams = {},
  ) =>
    this.request<SearchContentComicsResponseDto, any>({
      path: `/searchContent/searchComics`,
      method: "GET",
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags search-content-controller
   * @name SearchAni
   * @request GET:/searchContent/searchAni
   */
  searchAni = (
    query: {
      query: string;
      /** @format int32 */
      page?: number;
    },
    params: RequestParams = {},
  ) =>
    this.request<TmdbSearchTvDto, any>({
      path: `/searchContent/searchAni`,
      method: "GET",
      query: query,
      ...params,
    });
}
