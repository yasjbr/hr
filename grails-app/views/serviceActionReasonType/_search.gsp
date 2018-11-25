<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField class=""
                  name="descriptionInfo.localName" size="8"
                  label="${message(code:'serviceActionReasonType.descriptionInfo.localName.label',default:'localName')}"
                  value="${serviceActionReasonType?.descriptionInfo?.localName}" />
</el:formGroup>
<el:formGroup>
        <el:select name="isRelatedToEndOfService" size="8" class=""
                   label="${message(code: 'serviceActionReasonType.isRelatedToEndOfService.label', default: 'isRelatedToEndOfService')}"
                   from="['','true','false']" valueMessagePrefix="select"
                   placeholder="${message(code: 'default.select.label', default: 'please select')}"/>
</el:formGroup>
