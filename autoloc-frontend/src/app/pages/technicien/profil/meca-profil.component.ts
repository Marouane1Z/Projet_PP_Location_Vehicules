import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-meca-profil',
  templateUrl: './meca-profil.component.html',
  styleUrls: ['./meca-profil.component.scss']
})
export class MecaProfilComponent implements OnInit {
  ngOnInit(): void {}
}


