<el:formGroup>
    <el:checkboxField name="active" size="8" class=" isRequired"
                      label="${message(code: 'employeeSalaryInfo.active.label', default: 'active')}"
                      value="${employeeSalaryInfo?.active}" isChecked="${employeeSalaryInfo?.active}"/>
</el:formGroup>
<el:formGroup>

    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="bankBranchId"
                     label="${message(code: 'employeeSalaryInfo.bankBranchId.label', default: 'bankBranchId')}"
                     values="${[[employeeSalaryInfo?.bankBranchId, employeeSalaryInfo?.transientData?.bankBranchDTO]]}"
                     id="organizationAutoComplete"/>


</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="bankId"
                     label="${message(code: 'employeeSalaryInfo.bankId.label', default: 'bankId')}"
                     values="${[[employeeSalaryInfo?.bankBranchId, employeeSalaryInfo?.transientData?.bankDTO]]}"
                     id="bankId"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee"
                     action="autocomplete" name="employee.id"
                     label="${message(code: 'employeeSalaryInfo.employee.label', default: 'employee')}"
                     values="${[[employeeSalaryInfo?.employee?.id, employeeSalaryInfo?.employee?.descriptionInfo?.localName]]}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="firm"
                     action="autocomplete" name="firm.id"
                     label="${message(code: 'employeeSalaryInfo.firm.label', default: 'firm')}"
                     values="${[[employeeSalaryInfo?.firm?.id, employeeSalaryInfo?.firm?.descriptionInfo?.localName]]}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="internationalAccountNumber" size="8" class=" isRequired"
                  label="${message(code: 'employeeSalaryInfo.internationalAccountNumber.label', default: 'internationalAccountNumber')}"
                  value="${employeeSalaryInfo?.internationalAccountNumber}"/>
</el:formGroup>
<el:formGroup>

    <el:textField name="salary" size="8" class=" isRequired"
                  label="${message(code: 'employeeSalaryInfo.salary.label', default: 'salary')}"
                  value="${employeeSalaryInfo?.salary}"/>


</el:formGroup>



<el:formGroup>
    <el:select valueMessagePrefix="EnumSalaryClassification"
               from="${ps.gov.epsilon.aoc.enums.employee.v1.EnumSalaryClassification.values()}"
               name="salaryClassification" size="8" class=" isRequired"
               label="${message(code: 'employeeSalaryInfo.salaryClassification.label', default: 'salaryClassification')}"
               value="${employeeSalaryInfo?.salaryClassification}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="currency"
                     action="autocomplete"
                     name="salaryCurrencyId"
                     label="${message(code: 'employeeSalaryInfo.salaryCurrencyId.label', default: 'salaryCurrencyId')}"
                     values="${[[employeeSalaryInfo?.salaryCurrencyId, employeeSalaryInfo?.transientData?.currencyDTO]]}"
                     id="currencyAutoComplete"/>
</el:formGroup>