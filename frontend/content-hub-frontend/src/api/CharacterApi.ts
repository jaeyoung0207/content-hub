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
  AniListCharactersNodeDto,
  AniListStaffNodeDto,
} from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class CharacterApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags character-api
   * @name GetStaff
   * @summary 스태프 조회
   * @request GET:/api/character/getStaff
   */
  getStaff = (
    query: {
      /** @format int32 */
      staff_id: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<AniListStaffNodeDto, any>({
      path: `/api/character/getStaff`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags character-api
   * @name GetCharacter
   * @summary 캐릭터 조회
   * @request GET:/api/character/getCharacter
   */
  getCharacter = (
    query: {
      /** @format int32 */
      character_id: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<AniListCharactersNodeDto, any>({
      path: `/api/character/getCharacter`,
      method: 'GET',
      query: query,
      ...params,
    });
}
