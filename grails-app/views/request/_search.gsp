<el:formGroup>
    <el:textField name="id" size="6" class=" "
                  label="${message(code: 'request.id.label', default: 'id')}"
                  value=""/>

    <g:render template="/employee/wrapper" model="[isSearch     : true,
                                                   withOutForm  : true,
                                                   size         : 6]"/>
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

    <g:if test="${requestTypeList}">
        <el:select valueMessagePrefix="EnumRequestType" from="${requestTypeList}"
                   name="requestType" size="6" class=""
                   label="${message(code: 'request.requestType.label', default: 'requestType')}"/>
    </g:if>
    <g:else>
        <el:select valueMessagePrefix="EnumRequestType" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.values()}"
                   name="requestType" size="6" class=""
                   label="${message(code: 'request.requestType.label', default: 'requestType')}"/>
    </g:else>
</el:formGroup>
<el:formGroup>

    <el:range type="date" size="6" name="requestDate" setMinDateFromFor="requestDateTo"
              label="${message(code: 'periodSettlementRequest.requestDate.label')}"/>

    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="requestStatus" size="6" class=""
               label="${message(code: 'periodSettlementRequest.requestStatus.label', default: 'requestStatus')}"/>
</el:formGroup>

