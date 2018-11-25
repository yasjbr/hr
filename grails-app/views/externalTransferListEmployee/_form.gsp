<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee"
                     action="autocomplete" name="employee.id"
                     label="${message(code: 'externalTransferListEmployee.employee.label', default: 'employee')}"
                     values="${[[externalTransferListEmployee?.employee?.id, externalTransferListEmployee?.employee?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="organization"
                     action="autocomplete" name="toOrganizationId"
                     label="${message(code: 'externalTransferListEmployee.toOrganizationId.label', default: 'toOrganizationId')}"/>

</el:formGroup>


<el:formGroup>
    <el:dateField name="effectiveDate" size="8" class=" isRequired"
                  label="${message(code: 'externalTransferListEmployee.effectiveDate.label', default: 'effectiveDate')}"
                  value="${externalTransferListEmployee?.effectiveDate}"/>
</el:formGroup>

