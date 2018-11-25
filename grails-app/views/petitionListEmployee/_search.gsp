<el:formGroup>
    <el:textField name="petitionRequest.id" size="6" class=" "
                  label="${message(code: 'petitionRequest.id.label', default: 'id')}"
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
    <el:range type="date" name="requestDate" size="6" class="" setMinDateFromFor="dueRequestDate"
              label="${message(code: 'petitionRequest.requestDate.label', default: 'requestDate')}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="6"  class="" label="${message(code:'petitionListEmployee.recordStatus.label',default:'recordStatus')}" />
</el:formGroup>