import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IInterview } from 'app/entities/interview/interview.model';
import { Observable } from 'rxjs';

import { IPost } from '../post.model';
import { PostService } from '../service/post.service';
// import {} from '../../../account/account.module';

@Component({
  selector: 'jhi-applyforjobs',
  templateUrl: './applyforjobs.component.html',
  styleUrls: ['./applyforjobs.component.scss']
})
export class ApplyforjobsComponent implements OnInit {

  post: IPost | null = null;

  constructor(protected activatedRoute: ActivatedRoute, protected postService: PostService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => {
      this.post = post;
    });
  }

  previousState(): void {
    window.history.back();
  }

  applyButton(): void {
    this.postService.applyPost(this.post?.id?this.post.id:'').subscribe((response) => {
      console.error(response);
    });
    // debugger;
  }

}
