import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { DocumentsService } from '../service/documents.service';

import { DocumentsComponent } from './documents.component';

describe('Documents Management Component', () => {
  let comp: DocumentsComponent;
  let fixture: ComponentFixture<DocumentsComponent>;
  let service: DocumentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [DocumentsComponent],
    })
      .overrideTemplate(DocumentsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DocumentsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DocumentsService);

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
    expect(comp.documents?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
