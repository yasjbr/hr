<el:formGroup>

    <el:textField name="id" size="6"
                     class=""
                     label="${message(code: 'internalTransferRequest.id.label', default: 'id')}"/>

    <g:render template="/employee/wrapper" model="[isSearch     : true,
                                                   withOutForm  : true,
                                                   size         : 6]"/>

</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" controller="governorate" action="autocomplete"
                     name="fromGovernorate.id"
                     label="${message(code: 'internalTransferRequest.fromGovernorate.label', default: 'fromGovernorate')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="department" action="autocomplete"
                     name="fromDepartment.id"
                     label="${message(code: 'internalTransferRequest.fromDepartment.label', default: 'fromDepartment')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" controller="governorate" action="autocomplete"
                     name="toGovernorate.id"
                     label="${message(code: 'internalTransferRequest.toGovernorate.label', default: 'toGovernorate')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="department" action="autocomplete"
                     name="toDepartment.id"
                     label="${message(code: 'internalTransferRequest.toDepartment.label', default: 'toDepartment')}"/>

</el:formGroup>

<el:formGroup>

    <el:range type="date" size="6" name="effectiveDate"
              label="${message(code: 'internalTransferRequest.effectiveDate.label')}"/>

    <el:range type="date" size="6" name="requestDate"
              label="${message(code: 'internalTransferRequest.requestDate.label')}"/>
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

    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="requestStatus" size="6" class=""
               label="${message(code: 'internalTransferRequest.requestStatus.label', default: 'requestStatus')}"/>
</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" model="[hideExternalOrderInfo:true]"/>