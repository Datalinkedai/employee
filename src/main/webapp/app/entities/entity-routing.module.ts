import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'post',
        data: { pageTitle: 'employeeApp.post.home.title' },
        loadChildren: () => import('./post/post.module').then(m => m.PostModule),
      },
      {
        path: 'candidate',
        data: { pageTitle: 'employeeApp.candidate.home.title' },
        loadChildren: () => import('./candidate/candidate.module').then(m => m.CandidateModule),
      },
      {
        path: 'interview',
        data: { pageTitle: 'employeeApp.interview.home.title' },
        loadChildren: () => import('./interview/interview.module').then(m => m.InterviewModule),
      },
      {
        path: 'training',
        data: { pageTitle: 'employeeApp.training.home.title' },
        loadChildren: () => import('./training/training.module').then(m => m.TrainingModule),
      },
      {
        path: 'questions',
        data: { pageTitle: 'employeeApp.questions.home.title' },
        loadChildren: () => import('./questions/questions.module').then(m => m.QuestionsModule),
      },
      {
        path: 'options',
        data: { pageTitle: 'employeeApp.options.home.title' },
        loadChildren: () => import('./options/options.module').then(m => m.OptionsModule),
      },
      {
        path: 'tested',
        data: { pageTitle: 'employeeApp.tested.home.title' },
        loadChildren: () => import('./tested/tested.module').then(m => m.TestedModule),
      },
      {
        path: 'knowledge',
        data: { pageTitle: 'employeeApp.knowledge.home.title' },
        loadChildren: () => import('./knowledge/knowledge.module').then(m => m.KnowledgeModule),
      },
      {
        path: 'knowledge-central',
        data: { pageTitle: 'employeeApp.knowledgeCentral.home.title' },
        loadChildren: () => import('./knowledge-central/knowledge-central.module').then(m => m.KnowledgeCentralModule),
      },
      {
        path: 'documents',
        data: { pageTitle: 'employeeApp.documents.home.title' },
        loadChildren: () => import('./documents/documents.module').then(m => m.DocumentsModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
