<el:formGroup>

    <el:textField name="externalTransferRequest.id" size="6"
                  label="${message(code: 'externalTransferListEmployee.externalTransferRequest.id.label', default: 'externalTransferRequest id')}"/>


    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee"
                     action="autocomplete" name="employee.idList"
                     label="${message(code: 'externalTransferListEmployee.employee.label', default: 'employee')}"/>

</el:formGroup>



<el:formGroup>

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>


    <el:autocomplete optionKey="id" optionValue="name" size="6" controller="governorate" action="autocomplete"
                     name="fromGovernorateIdList"
                     label="${message(code: 'externalTransferRequest.fromGovernorate.label', default: 'fromGovernorate')}"/>

</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="department" action="autocomplete"
                     name="fromDepartmentIdList"
                     label="${message(code: 'externalTransferRequest.fromDepartment.label', default: 'fromDepartment')}"/>



    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     controller="organization" action="autocomplete"
                     name="toOrganizationIdList"
                     label="${message(code: 'externalTransferRequest.toOrganizationId.label', default: 'toOrganizationId')}"/>

</el:formGroup>


<el:formGroup>

    <el:range type="date" name="effectiveDate" size="6" class=""
              label="${message(code: 'externalTransferListEmployee.effectiveDate.label', default: 'effectiveDate')}"/>
    <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}"
               name="recordStatus" size="6" class=""
               label="${message(code: 'externalTransferRequest.recordStatus.label', default: 'recordStatus')}"/>
</el:formGroup>

