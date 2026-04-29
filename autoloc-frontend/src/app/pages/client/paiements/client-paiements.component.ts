import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-client-paiements',
  templateUrl: './client-paiements.component.html',
  styleUrls: ['./client-paiements.component.scss']
})
export class ClientPaiementsComponent implements OnInit {
  ngOnInit(): void {}
}


