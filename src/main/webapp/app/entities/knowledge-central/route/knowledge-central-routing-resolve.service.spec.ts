import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IKnowledgeCentral, KnowledgeCentral } from '../knowledge-central.model';
import { KnowledgeCentralService } from '../service/knowledge-central.service';

import { KnowledgeCentralRoutingResolveService } from './knowledge-central-routing-resolve.service';

describe('KnowledgeCentral routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: KnowledgeCentralRoutingResolveService;
  let service: KnowledgeCentralService;
  let resultKnowledgeCentral: IKnowledgeCentral | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(KnowledgeCentralRoutingResolveService);
    service = TestBed.inject(KnowledgeCentralService);
    resultKnowledgeCentral = undefined;
  });

  describe('resolve', () => {
    it('should return IKnowledgeCentral returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultKnowledgeCentral = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultKnowledgeCentral).toEqual({ id: 'ABC' });
    });

    it('should return new IKnowledgeCentral if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultKnowledgeCentral = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultKnowledgeCentral).toEqual(new KnowledgeCentral());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as KnowledgeCentral })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultKnowledgeCentral = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultKnowledgeCentral).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
