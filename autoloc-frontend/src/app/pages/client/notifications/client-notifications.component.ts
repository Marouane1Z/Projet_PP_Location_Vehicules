import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/services/auth.service';
import { Notification } from '../../../core/models/user.model';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-client-notifications',
  templateUrl: './client-notifications.component.html',
  styleUrls: ['./client-notifications.component.scss']
})
export class ClientNotificationsComponent implements OnInit {
  notifications: Notification[] = [];

  constructor(private notifService: NotificationService, private auth: AuthService) {}

  ngOnInit(): void {
    this.notifService.getByUser(this.auth.userId).subscribe({
      next: (data) => this.notifications = data,
      error: () => this.notifications = []
    });
  }

  marquerLue(notif: Notification): void {
    if (notif.lue) return;
    this.notifService.marquerLue(notif.id).subscribe(() => notif.lue = true);
  }

  marquerToutesLues(): void {
    this.notifications.filter(n => !n.lue).forEach(n => this.marquerLue(n));
  }

  get nonLues(): number { return this.notifications.filter(n => !n.lue).length; }
}


