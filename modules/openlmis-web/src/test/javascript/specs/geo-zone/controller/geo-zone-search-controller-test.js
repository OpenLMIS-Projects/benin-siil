/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Geographic Zone Search Controller", function () {

  var scope, $httpBackend, ctrl, navigateBackService;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    scope.query = "Nod";
    navigateBackService = _navigateBackService_;
    navigateBackService.query = '';
    ctrl = $controller;
    ctrl('GeoZoneSearchController', {$scope: scope});
  }));

  it('should get all geo zones in a page depending on search criteria', function () {
    var geoZone = {"code": "G1", "name": "Zone 1"};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"geoZones": [geoZone], "pagination": pagination};
    scope.query = "Nod";
    scope.selectedSearchOption = {"value": 'name'};
    $httpBackend.when('GET', '/geographicZones.json?columnName=name&page=1&searchParam=' + scope.query).respond(response);
    scope.search(1);
    $httpBackend.flush();

    expect(scope.geoZoneList).toEqual([geoZone]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.resultCount).toEqual(100);
  });

  it('should clear search param and result list', function () {
    var geoZone = {"code": "N1", "name": "Node 1"};
    scope.query = "query";
    scope.resultCount = 100;
    scope.geoZoneList = [geoZone];
    scope.showResults = true;

    scope.clearSearch();

    expect(scope.showResults).toEqual(false);
    expect(scope.query).toEqual("");
    expect(scope.resultCount).toEqual(0);
    expect(scope.geoZoneList).toEqual([]);
  });

  it('should trigger search on enter key', function () {
    var event = {"keyCode": 13};
    var searchSpy = spyOn(scope, 'search');

    scope.triggerSearch(event);

    expect(searchSpy).toHaveBeenCalledWith(1);
  });

  it('should set selected search option', function () {
    var searchOption = "search_option";

    scope.selectSearchType(searchOption)

    expect(scope.selectedSearchOption).toEqual(searchOption)
  });

  it('should set query according to navigate back service', function () {
    scope.query = '';
    navigateBackService.query = 'query';

    scope.$emit('$viewContentLoaded');

    expect(scope.query).toEqual("query");
  });

  it('should get results according to specified page', function () {
    scope.currentPage = 5;
    var searchSpy = spyOn(scope, 'search');

    scope.$apply(function () {
      scope.currentPage = 6;
    });

    expect(searchSpy).toHaveBeenCalledWith(6);
  });

});