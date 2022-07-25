import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IKnowledge, Knowledge } from '../knowledge.model';
import { KnowledgeService } from '../service/knowledge.service';

import { KnowledgeRoutingResolveService } from './knowledge-routing-resolve.service';

describe('Knowledge routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: KnowledgeRoutingResolveService;
  let service: KnowledgeService;
  let resultKnowledge: IKnowledge | undefined;

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
    routingResolveService = TestBed.inject(KnowledgeRoutingResolveService);
    service = TestBed.inject(KnowledgeService);
    resultKnowledge = undefined;
  });

  describe('resolve', () => {
    it('should return IKnowledge returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultKnowledge = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultKnowledge).toEqual({ id: 'ABC' });
    });

    it('should return new IKnowledge if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultKnowledge = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultKnowledge).toEqual(new Knowledge());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Knowledge })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultKnowledge = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultKnowledge).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
