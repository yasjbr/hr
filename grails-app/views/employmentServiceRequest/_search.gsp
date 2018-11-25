<el:formGroup>
    <el:textField name="id"
                  size="6"
                  class=" "
                  label="${message(code: 'employmentServiceRequest.id.label', default: 'id')}"
                  value=""/>
    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>
</el:formGroup>
<el:formGroup>
    <el:range type="date" size="6" name="requestDate" setMinDateFromFor="requestDateTo"
              label="${message(code: 'employmentServiceRequest.requestDate.label')}"/>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=""
                     controller="serviceActionReason"
                     paramsGenerateFunction="reasonParams"
                     action="autocomplete"
                     name="serviceActionReason.id"
                     label="${message(code: 'employmentServiceRequest.serviceActionReason.label', default: 'serviceActionReason')}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestStatus"
               from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="requestStatus"
               size="6"
               class=""
               label="${message(code: 'employmentServiceRequest.requestStatus.label', default: 'requestStatus')}"/>

    <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
    </sec:ifAnyGranted>
</el:formGroup>

<g:render template="/request/wrapperManagerialOrder" />

<script>
    function reasonParams() {
        return {
            "firm.id": "${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}",
            "isRelatedToEndOfService_string": "${endOfServiceFlag}"
        };
    }
</script>