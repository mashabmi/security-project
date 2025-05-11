import { Component, ViewChild } from "@angular/core";
import { AuthService } from "../_services/auth.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [FormsModule, CommonModule],
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})

export class RegisterComponent {

    form: any = {
        username: null,
        email: null,
        password: null,
        confirmPassword: null,
        name: null,
        lastName: null,
        address: null,
        city: null,
        country: null,
        phoneNumber: null,
        companyName: null,
        pib: null,
        clientType: 'INDIVIDUAL', 
        servicesPackage: 'BASIC',
        userRole: 'USER'
    };
    isSuccessful = false;
    isSignUpFailed = false;
    errorMessage = '';
    passwordsMismatch = false;

    constructor(private authService: AuthService, private router: Router) {}

    getErrorMessage(field: string): string {
        if (!this.form[field]) {
            return 'This field is required.';
        }
    
        if (field === 'email' && !this.form.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
            return 'Incorrect email address format.';
        }
    
        if (field === 'password' && this.form.password.length < 6) {
            return 'Password must have at least 6 characters.';
        }
    
        if (field === 'confirmPassword' && this.form.password !== this.form.confirmPassword) {
            return 'The passwords do not match.';
        }
    
        return '';
    }    

    
    onSubmit(): void {
        if (this.form.password !== this.form.confirmPassword) {
            this.passwordsMismatch = true;
            return;
          } else {
            this.passwordsMismatch = false;
          }
        const {
            name,
            lastName,
            username,
            email,
            password,
            confirmPassword,
            address,
            city,
            country,
            phoneNumber,
            companyName,
            pib,
            clientType,
            servicesPackage
        } = this.form;
        
        this.authService.register(name, lastName, username, email, password, confirmPassword, address, city, country, phoneNumber, companyName, pib, clientType, servicesPackage).subscribe({
            next: data => {
                console.log(data);
                this.isSuccessful = true;
                this.isSignUpFailed = false;
                setTimeout(() => {
                    this.router.navigate(['/login']);
                }, 2000);

            },
            error: err => {
                this.errorMessage = err.error.message;
                this.isSignUpFailed = true;
            },
        });
    }
}