<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField class=""
                  name="descriptionInfo.localName" size="8"
                  label="${message(code:'serviceActionReason.descriptionInfo.localName.label',default:'localName')}"
                  value="${serviceActionReason?.descriptionInfo?.localName}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="serviceActionReasonType"
                     action="autocomplete" name="serviceActionReasonType.id"
                     label="${message(code: 'serviceActionReason.serviceActionReasonType.label', default: 'serviceActionReasonType')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employeeStatus"
                     action="autocomplete" name="employeeStatusResult.id"
                     label="${message(code: 'serviceActionReason.employeeStatusResult.label', default: 'employeeStatusResult')}"/>
</el:formGroup>
<el:formGroup>
    <el:select name="allowReturnToService" size="8" class=""
               label="${message(code: 'serviceActionReason.allowReturnToService.label', default: 'allowReturnToService')}"
               from="['','true','false']" valueMessagePrefix="select"
               placeholder="${message(code: 'default.select.label', default: 'please select')}"/>
</el:formGroup>
