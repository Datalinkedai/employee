import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPost } from '../post.model';
// import {} from '../../../account/account.module';

@Component({
  selector: 'jhi-applyforjobs',
  templateUrl: './applyforjobs.component.html',
  styleUrls: ['./applyforjobs.component.scss']
})
export class ApplyforjobsComponent implements OnInit {

  post: IPost | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => {
      this.post = post;
    });
  }

  previousState(): void {
    window.history.back();
  }

  applyButton(): void {
    console.error(this.post?.id);
    // debugger;
  }

}
