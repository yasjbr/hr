<el:formGroup>
    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="financialNumber" size="6" class=""
                  label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" " controller="firm"
                     action="autocomplete" name="firm.id"
                     label="${message(code: 'employeeSalaryInfo.firm.label', default: 'firm')}"
                     values="${[[employeeSalaryInfo?.firm?.id, employeeSalaryInfo?.firm?.descriptionInfo?.localName]]}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" "
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="bankId"
                     paramsGenerateFunction="bankParentOrganizationParams"
                     label="${message(code: 'employeeSalaryInfo.bankId.label', default: 'bankId')}"
                     values="${[[employeeSalaryInfo?.bankBranchId, employeeSalaryInfo?.transientData?.bankDTO]]}"
                     id="bankId"/>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" "
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="bankBranchId"
                     paramsGenerateFunction="bankOrganizationParams"
                     label="${message(code: 'employeeSalaryInfo.bankBranchId.label', default: 'bankBranchId')}"
                     values="${[[employeeSalaryInfo?.bankBranchId, employeeSalaryInfo?.transientData?.bankBranchDTO]]}"
                     id="organizationAutoComplete"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="internationalAccountNumber" size="6" class=" "
                  label="${message(code: 'employeeSalaryInfo.internationalAccountNumber.label', default: 'internationalAccountNumber')}"
                  value="${employeeSalaryInfo?.internationalAccountNumber}"/>

    <el:textField name="bankAccountNumber" size="6" class=" "
                  label="${message(code: 'employeeSalaryInfo.bankAccountNumber.label', default: 'bankAccountNumber')}"
                  value="${employeeSalaryInfo?.bankAccountNumber}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumSalaryClassification"
               from="${ps.gov.epsilon.aoc.enums.employee.v1.EnumSalaryClassification.values()}"
               name="salaryClassification" size="6" class=" "
               label="${message(code: 'employeeSalaryInfo.salaryClassification.label', default: 'salaryClassification')}"
               value="${employeeSalaryInfo?.salaryClassification}"/>
    <el:integerField name="salary" size="6" class=" isNumber"
                  label="${message(code: 'employeeSalaryInfo.salary.label', default: 'salary')}"
                  value="${employeeSalaryInfo?.salary}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" "
                     controller="currency"
                     action="autocomplete"
                     name="salaryCurrencyId"
                     label="${message(code: 'employeeSalaryInfo.salaryCurrencyId.label', default: 'salaryCurrencyId')}"
                     values="${[[employeeSalaryInfo?.salaryCurrencyId, employeeSalaryInfo?.transientData?.currencyDTO]]}"
                     id="currencyAutoComplete"/>

    <el:range type="date" size="6" name="salaryDate" label="${message(code: 'employeeSalaryInfo.salaryDate.label')}"/>
</el:formGroup>


<script>
    function bankParentOrganizationParams() {
        return {
            "organizationType.id": "${ps.police.pcore.enums.v1.OrganizationTypeEnum.BANK.value()}",
            "justParents": "true"
        };
    }
    function bankOrganizationParams() {
        return {
            "parentOrganization.id": $('#bankId').val(),
            "organizationType.id": "${ps.police.pcore.enums.v1.OrganizationTypeEnum.BANK.value()}",
            "justChilds": "true"
        };
    }
</script>

%{--<el:formGroup>--}%
    %{--<el:checkboxField name="active" size="6" class=" "--}%
                      %{--label="${message(code: 'employeeSalaryInfo.active.label', default: 'active')}"--}%
                      %{--value="${employeeSalaryInfo?.active}" isChecked="${employeeSalaryInfo?.active}"/>--}%
%{--</el:formGroup>--}%