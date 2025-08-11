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

import { AniListCharactersNodesDto } from "./data-contracts";
import { HttpClient, RequestParams } from "./http-client";

export class Character<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
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
    params: RequestParams = {},
  ) =>
    this.request<AniListCharactersNodesDto, any>({
      path: `/character/getCharacter`,
      method: "GET",
      query: query,
      ...params,
    });
}
