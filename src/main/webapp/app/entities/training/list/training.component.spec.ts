import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TrainingService } from '../service/training.service';

import { TrainingComponent } from './training.component';

describe('Training Management Component', () => {
  let comp: TrainingComponent;
  let fixture: ComponentFixture<TrainingComponent>;
  let service: TrainingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TrainingComponent],
    })
      .overrideTemplate(TrainingComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrainingComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TrainingService);

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
    expect(comp.trainings?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
