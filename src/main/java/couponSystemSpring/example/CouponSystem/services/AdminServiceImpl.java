package couponSystemSpring.example.CouponSystem.services;

import couponSystemSpring.example.CouponSystem.beans.Company;
import couponSystemSpring.example.CouponSystem.beans.Coupon;
import couponSystemSpring.example.CouponSystem.beans.Customer;
import couponSystemSpring.example.CouponSystem.exceptions.CouponSystemException;
import couponSystemSpring.example.CouponSystem.exceptions.ErrorMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl extends ClientService implements AdminService{
    @Override
    public boolean login(String email, String password) {
        return email.equals("admin@admin.com") && password.equals("admin");
    }
    @Override
    public Company add(Company company) throws Exception {
        int id = company.getId();
        String name = company.getName();
        String email = company.getEmail();
        if (this.companyRepository.existsById(id)) {
            throw new CouponSystemException(ErrorMessage.ADD_COMPANY_ID_ALREADY_EXISTS);
        }
        if (this.companyRepository.existsByName(name)) {
            throw new CouponSystemException(ErrorMessage.ADD_COMPANY_NAME_ALREADY_EXISTS);
        }
        if (this.companyRepository.existsByEmail(email)) {
            throw new CouponSystemException(ErrorMessage.ADD_COMPANY_EMAIL_ALREADY_EXISTS);
        }
        return this.companyRepository.save(company);
    }

    @Override
    public void updateCompany(Company company) throws CouponSystemException {
        int companyId = company.getId();
        if (!companyRepository.existsById(companyId)) {
            throw new CouponSystemException(ErrorMessage.UPDATE_COMPANY_ID_NOT_EXISTS);
        }
        Company fromDb = this.companyRepository.findById(companyId);
        if (!fromDb.getName().equals(company.getName())) {
            throw new CouponSystemException(ErrorMessage.UPDATE_COMPANY_CANNOT_UPDATE_NAME);
        }
        this.companyRepository.saveAndFlush(company);
    }
    @Override
    public void deleteCompany(int companyId) throws CouponSystemException {
        if (!companyRepository.existsById(companyId)) {
            throw new CouponSystemException(ErrorMessage.CANNOT_DELETE_COMPANY_ID_NOT_EXISTS);
        }
        Company company = companyRepository.findById(companyId);
        List<Coupon>companyCoupons = couponRepository.findByCompany_Id(companyId);
        for (Coupon coupon:companyCoupons) {
            if (!coupon.getCustomers().isEmpty()){
                for (Customer customer: coupon.getCustomers()) {
                    customer.getCoupons().remove(coupon);
                    customerRepository.save(customer);
                }
            }

            coupon.setCustomers(new ArrayList<>());
        }
        companyRepository.delete(company);
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Optional<Company> getSingleCompany(int companyId) throws CouponSystemException {
        if (!companyRepository.existsById(companyId)) {
            throw new CouponSystemException(ErrorMessage.CANNOT_FIND_COMPANY_ID);
        }
        return Optional.ofNullable(this.companyRepository.findById(companyId));
    }

    @Override
    public Customer addCustomer(Customer customer) throws CouponSystemException {
        int customerId = customer.getId();
        String email = customer.getEmail();
        if (customerRepository.existsById(customerId)) {
            throw new CouponSystemException(ErrorMessage.CUSTOMER_ID_ALREADY_EXISTS);
        }
        if (customerRepository.existsByEmail(email)) {
            throw new CouponSystemException(ErrorMessage.CUSTOMER_EMAIL_ALREADY_EXISTS);
        }
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(int customerId, Customer customer) throws CouponSystemException {
        customer.setId(customerId);
        System.out.println("id"+customer.getId());
        if (customer.getId() != customerId) {
            throw new CouponSystemException(ErrorMessage.CANNOT_UPDATE_CUSTOMER_ID);
        }
        customerRepository.saveAndFlush(customer);
        return customer;
    }

    @Override
    public void deleteCustomer(int customerId) throws CouponSystemException {
        if (customerRepository.existsById(customerId)) {
            Customer customer = customerRepository.findById(customerId).orElseThrow();
            customerRepository.delete(customer);
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Optional<Customer>> getSingleCustomer(int customerId) throws CouponSystemException {
        if (!customerRepository.existsById(customerId)){
            throw new CouponSystemException(ErrorMessage.CANNOT_FIND_CUSTOMER_ID);
        }
        return Optional.ofNullable(customerRepository.findById(customerId));
    }
}
