import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { QuestionsService } from '../service/questions.service';

import { QuestionsComponent } from './questions.component';

describe('Questions Management Component', () => {
  let comp: QuestionsComponent;
  let fixture: ComponentFixture<QuestionsComponent>;
  let service: QuestionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [QuestionsComponent],
    })
      .overrideTemplate(QuestionsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuestionsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(QuestionsService);

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
    expect(comp.questions?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
