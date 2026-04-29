import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-admin-rapports',
  templateUrl: './admin-rapports.component.html',
  styleUrls: ['./admin-rapports.component.scss']
})
export class AdminRapportsComponent implements OnInit {
  ngOnInit(): void {}
}


