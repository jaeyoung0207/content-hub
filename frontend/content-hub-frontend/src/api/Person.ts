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

export class Person<
  SecurityDataType = unknown,
> extends HttpClient<SecurityDataType> {
  /**
   * No description
   *
   * @tags person-controller
   * @name GetPersonDetails
   * @request GET:/person/details
   */
  getPersonDetails = (
    query: {
      /** @format int32 */
      personId: number;
    },
    params: RequestParams = {}
  ) =>
    this.request<PersonResponseDto, any>({
      path: `/person/details`,
      method: 'GET',
      query: query,
      ...params,
    });
}
