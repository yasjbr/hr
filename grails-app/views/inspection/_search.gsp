
<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>

%{--
<g:render template="/DescriptionInfo/wrapper" model="[bean: inspection?.descriptionInfo, isSearch: true]"/>
--}%
<el:formGroup>
<el:textField class=""
              name="descriptionInfo.localName" size="${size?:"8"}"
              label="${messageValue?:(message(code:'descriptionInfo.localName.label',default:'localName'))}"
              value="${bean?.localName}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="inspectionCategory"
                     action="autocomplete" name="inspectionCategory.id"
                     label="${message(code: 'inspection.inspectionCategory.label', default: 'inspectionCategory')}"/>
</el:formGroup>
<el:formGroup>
    <el:select name="hasMark" size="8" class=""
               label="${message(code: 'inspection.hasMark.label', default: 'hasMark')}"
               from="['','true','false']" valueMessagePrefix="select"
               placeholder="${message(code: 'default.select.label', default: 'please select')}"
    />
</el:formGroup>
<el:formGroup>
    <el:select name="hasPeriod" size="8" class=""
                      label="${message(code: 'inspection.hasPeriod.label', default: 'hasPeriod')}"
                      from="['','true','false']" valueMessagePrefix="select"
                      placeholder="${message(code: 'default.select.label', default: 'please select')}"/>
</el:formGroup>
<el:formGroup>
    <el:select name="hasDates" size="8" class=""
                      label="${message(code: 'inspection.hasDates.label', default: 'hasDates')}"
                      from="['','true','false']" valueMessagePrefix="select"
                      placeholder="${message(code: 'default.select.label', default: 'please select')}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="orderId" size="8" class=""
                     label="${message(code: 'inspection.orderId.label', default: 'orderId')}"/>
</el:formGroup>
%{--<el:formGroup>
    <el:checkboxField name="isIncludedInLists" size="8" class=""
                      label="${message(code: 'inspection.isIncludedInLists.label', default: 'isIncludedInLists')}"/>
</el:formGroup>--}%

%{--<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="committeeRole"
                     action="autocomplete" multiple="true" name="committeeRoles.id"
                     label="${message(code: 'inspection.committeeRole.label', default: 'committeeRoles')}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea
            name="description"
            size="8" class=""
            label="${message(code: 'inspection.description.label', default: 'description')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'inspection.universalCode.label', default: 'universalCode')}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class="" label="${message(code: 'inspection.note.label', default: 'note')}"/>
</el:formGroup>--}%


