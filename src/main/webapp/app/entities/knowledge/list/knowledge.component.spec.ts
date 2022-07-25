import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { KnowledgeService } from '../service/knowledge.service';

import { KnowledgeComponent } from './knowledge.component';

describe('Knowledge Management Component', () => {
  let comp: KnowledgeComponent;
  let fixture: ComponentFixture<KnowledgeComponent>;
  let service: KnowledgeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [KnowledgeComponent],
    })
      .overrideTemplate(KnowledgeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(KnowledgeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(KnowledgeService);

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
    expect(comp.knowledges?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
