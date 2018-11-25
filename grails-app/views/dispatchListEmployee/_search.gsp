
<el:formGroup>
    <g:render template="/employee/wrapper" model="[isSearch: true, withOutForm: true, size: 6]"/>
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
    <el:autocomplete optionKey="id" optionValue="name" size="6"
                     controller="pcore"
                     action="governorateAutoComplete"
                     name="governorateIdList"
                     label="${message(code:'dispatchRequest.governorate.label',default:'governorate')}" />
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""  controller="department" action="autocomplete"
                     name="departmentIdList" label="${message(code:'dispatchRequest.department.label',default:'fromDepartment')}" />
</el:formGroup>

<el:formGroup>
    <el:integerField name="periodInMonths" size="6" class=" isNumber" label="${message(code: 'dispatchRequest.periodInMonths.label', default: 'periodInMonths')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="fromDate" setMinDateFromFor="fromDateTo"
              label="${message(code: 'dispatchRequest.fromDate.label')}"/>

    <el:range type="date" size="6" name="toDate" setMinDateFromFor="toDateTo"
              label="${message(code: 'dispatchRequest.toDate.label')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="nextVerificationDate" setMinDateFromFor="nextVerificationDateTo"
              label="${message(code: 'dispatchRequest.nextVerificationDate.label')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="organization"
                     action="autocomplete"
                     name="organizationId"
                     label="${message(code: 'dispatchRequest.organization.label', default: 'educationMajorId')}"
                     values=""/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="educationMajor"
                     action="autocomplete"
                     name="educationMajorId"
                     label="${message(code: 'dispatchRequest.educationMajor.label', default: 'educationMajorId')}"
                     values=""/>
    <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}"
               name="recordStatus" size="6" class=""
               label="${message(code: 'dispatchListEmployee.recordStatus.label', default: 'recordStatus')}"/>
</el:formGroup>