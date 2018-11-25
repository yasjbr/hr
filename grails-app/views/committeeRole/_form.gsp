
<g:render template="/DescriptionInfo/wrapper" model="[bean:committeeRole?.descriptionInfo]" />

<el:formGroup>
    <el:textArea size="8" name="note" class=" "  label="${message(code:'committeeRole.note.label',default:'note')}" value="${committeeRole?.note}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'committeeRole.universalCode.label',default:'universalCode')}" value="${committeeRole?.universalCode}"/>
</el:formGroup>