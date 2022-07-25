import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { KnowledgeCentralService } from '../service/knowledge-central.service';

import { KnowledgeCentralComponent } from './knowledge-central.component';

describe('KnowledgeCentral Management Component', () => {
  let comp: KnowledgeCentralComponent;
  let fixture: ComponentFixture<KnowledgeCentralComponent>;
  let service: KnowledgeCentralService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [KnowledgeCentralComponent],
    })
      .overrideTemplate(KnowledgeCentralComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(KnowledgeCentralComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(KnowledgeCentralService);

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
    expect(comp.knowledgeCentrals?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
