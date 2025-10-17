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

import { PersonResponseDto } from './data-contracts';
import { HttpClient, RequestParams } from './http-client';

export class PersonApi<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags person-api
   * @name GetPersonDetails
   * @summary 인물 상세 정보 조회
   * @request GET:/api/person/getPersonDetails
   */
  getPersonDetails = (
    query: {
      /** @format int32 */
      person_id: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<PersonResponseDto, any>({
      path: `/api/person/getPersonDetails`,
      method: 'GET',
      query: query,
      ...params,
    });
}
