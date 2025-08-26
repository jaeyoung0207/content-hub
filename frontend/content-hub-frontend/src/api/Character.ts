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

export class Character<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags character-controller
   * @name GetStaff
   * @request GET:/character/getStaff
   */
  getStaff = (
    query: {
      /** @format int32 */
      staffId: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<AniListStaffNodeDto, any>({
      path: `/character/getStaff`,
      method: 'GET',
      query: query,
      ...params,
    });
  /**
   * No description
   *
   * @tags character-controller
   * @name GetCharacter
   * @request GET:/character/getCharacter
   */
  getCharacter = (
    query: {
      /** @format int32 */
      characterId: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<AniListCharactersNodeDto, any>({
      path: `/character/getCharacter`,
      method: 'GET',
      query: query,
      ...params,
    });
}
