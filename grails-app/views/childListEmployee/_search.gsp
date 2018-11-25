<el:formGroup>
    <el:textField name="id" size="6" class=" "
                     label="${message(code: 'childRequest.id.label', default: 'id')}"
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
    <el:textField
            name="financialNumber"
            size="6"
            class=""
            label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"
            value=""/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="person"
            action="autocomplete"
            name="relatedPersonId"
            label="${message(code: 'childRequest.transientData.relatedPersonDTO.localFullName.label', default: 'relatedPersonId')}"
            values=""/>
    <el:range type="date" name="requestDate" size="6" class="" setMinDateFromFor="dueRequestDate"
              label="${message(code: 'childRequest.requestDate.label', default: 'requestDate')}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}"
               name="recordStatus" size="6" class=""
               label="${message(code: 'childListEmployee.recordStatus.label', default: 'recordStatus')}"/>
</el:formGroup>
