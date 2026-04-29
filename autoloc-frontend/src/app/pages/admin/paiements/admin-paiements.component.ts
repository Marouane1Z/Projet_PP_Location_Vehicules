import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-admin-paiements',
  templateUrl: './admin-paiements.component.html',
  styleUrls: ['./admin-paiements.component.scss']
})
export class AdminPaiementsComponent implements OnInit {
  ngOnInit(): void {}
}


