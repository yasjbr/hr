<el:formGroup>
    <g:if test="${manageSearch}">
        <el:textField name="suspensionExtensionRequest.id" size="6" class=""
                      label="${message(code: 'suspensionExtensionRequest.id.label', default: 'id')}"/>
    </g:if><g:else>
    <el:textField name="id" size="6" class=""
                  label="${message(code: 'suspensionExtensionRequest.id.label', default: 'id')}"/>

</g:else>

    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>

</el:formGroup>


<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>



    <el:select valueMessagePrefix="EnumSuspensionType"
               from="${ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.values()}" name="suspensionType"
               size="6" class=""
               label="${message(code: 'suspensionExtensionRequest.suspensionType.label', default: 'suspensionType')}"/>

</el:formGroup>



<el:formGroup>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'suspensionExtensionRequest.fromDate.label')}"/>


    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'suspensionExtensionRequest.toDate.label')}"/>

</el:formGroup>

<el:formGroup>
    <el:integerField name="periodInMonth" size="6" class=" isNumber"
                     label="${message(code: 'suspensionExtensionRequest.periodInMonth.label', default: 'periodInMonth')}"/>

    <el:select valueMessagePrefix="EnumListRecordStatus"
               from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}"
               name="recordStatus" size="6" class=""
               label="${message(code: 'suspensionRequest.requestStatus.label', default: 'requestStatus')}"/>
</el:formGroup>