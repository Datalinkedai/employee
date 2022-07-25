import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TestedService } from '../service/tested.service';

import { TestedComponent } from './tested.component';

describe('Tested Management Component', () => {
  let comp: TestedComponent;
  let fixture: ComponentFixture<TestedComponent>;
  let service: TestedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TestedComponent],
    })
      .overrideTemplate(TestedComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TestedComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TestedService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.testeds?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
