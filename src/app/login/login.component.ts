import { Component, OnDestroy, OnInit } from "@angular/core";
import { AuthService } from "../_services/auth.service";
import { StorageService } from "../_services/storage.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { UserRole } from "../_enums/user-role.enum";
import { Router } from "@angular/router";

declare var grecaptcha: any;

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [FormsModule, CommonModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

    form: any = {
        username: null,
        password: null,
        role: null,
        captcha: null
    };
    UserRole = UserRole;
    isLoggedIn = false;
    isLoginFailed = false;
    errorMessage = '';
    roles: string[] = [];
    private recaptchaWidgetId: number | null = null;

    constructor(private authService: AuthService, private storageService: StorageService, private router: Router) {}

    ngOnInit(): void {
        if(this.storageService.isLoggedIn()) {
            this.isLoggedIn = true;
            this.roles = this.storageService.getUser().roles;
        }

        (window as any).onCaptchaResolved = (response: string) => {
            this.form.captcha = response;
        };

    }

    ngOnDestroy(): void {
        this.cleanupRecaptcha();
    }

    onRoleChange(): void {
        if (this.form.role === UserRole.MODERATOR) {
            this.loadRecaptchaScript();
        } else {
            this.cleanupRecaptcha();
            this.form.captcha = null;
        }
    }

    loadRecaptchaScript(): void {
        if (typeof grecaptcha === 'undefined') {
            const script = document.createElement('script');
            script.src = 'https://www.google.com/recaptcha/api.js?onload=onRecaptchaLoad&render=explicit';
            script.async = true;
            script.defer = true;
            
            (window as any).onRecaptchaLoad = () => {
                this.renderRecaptcha();
            };
            
            document.body.appendChild(script);
        } else {
            this.renderRecaptcha();
        }
    }

    private renderRecaptcha(): void {
        setTimeout(() => {
            const container = document.querySelector('.g-recaptcha');
            if (container && typeof grecaptcha !== 'undefined') {
                this.recaptchaWidgetId = grecaptcha.render(container, {
                    sitekey: '6LdqIfcqAAAAABo4RSS7b6YLCznOCYL7YVfblnOQ',
                    callback: (response: string) => {
                        this.form.captcha = response;
                    },
                    'expired-callback': () => {
                        this.form.captcha = null;
                    },
                    'error-callback': () => {
                        this.form.captcha = null;
                    }
                });
            }
        }, 100);
    }

    private cleanupRecaptcha(): void {
        if (this.recaptchaWidgetId !== null && typeof grecaptcha !== 'undefined') {
            grecaptcha.reset(this.recaptchaWidgetId);
        }
    }

    onSubmit(): void {
    
        const {username, password, role, captcha} = this.form;

        if (!username || !password || !role) {
            this.isLoginFailed = true;
            return;
        }

        if(role === UserRole.MODERATOR && !captcha) {
            this.isLoginFailed = true;
            return;
        }

        this.isLoginFailed = false;

        this.authService.login(username, password, role, captcha).subscribe({
            next: data => {
                if (!data.roles.includes(role)) {
                    this.errorMessage = `No permission to login as ${this.getRole(role)}.`;
                    this.isLoginFailed = true;
                    this.cleanupRecaptcha();
                    return;
                }

                this.storageService.saveUser(data);
                this.isLoginFailed = false;
                this.isLoggedIn = true;
                this.roles = this.storageService.getUser().roles;
                
                this.router.navigate(['/profile']).then(() => {
                    window.location.reload();
                });
            },
            error: err => {
                this.errorMessage = err.error?.message || err.message || 'Login failed. Please try again.';
                this.isLoginFailed = true;
                this.cleanupRecaptcha();
                this.form.captcha = null;
            }
        });
    }

    getRole(role: UserRole): string {
        switch(role) {
            case UserRole.ADMIN: return 'administrator';
            case UserRole.MODERATOR: return 'moderator';
            case UserRole.USER: return 'user';
            default: return 'this role';
        }
    }
}



