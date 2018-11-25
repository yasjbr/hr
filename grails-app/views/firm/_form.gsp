<el:formGroup>
    <el:textField name="code" size="8" class=" isRequired" label="${message(code: 'firm.code.label', default: 'code')}"
                  value="${firm?.code}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="name" size="8" class=" isRequired" label="${message(code: 'firm.name.label', default: 'name')}"
                  value="${firm?.name}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="coreOrganizationId"
                     label="${message(code: 'firm.coreOrganizationId.label', default: 'coreOrganizationId')}"
                     values="${[[firm?.coreOrganizationId, firm?.transientData?.coreName]]}"
                     id="organizationAutoComplete"/>


%{--  hide add button
<el:modal buttons="${buttons}" id="modal-form" title="${message(code:'firm.createOrganization.label',default:'create organization')}" width="60%"
          buttonClass=" btn btn-sm btn-primary" hideCancel="true" buttonLabel="+ إضافة">
    <el:modalButton class="btn-sm btn-primary" icon="ace-icon fa fa-check" messageCode="default.button.create.label" onClick="saveOrganization();" />

    <el:formGroup>

        <div id="organizationId">
            <g:render template="/pcore/organization/form"/>
        </div>
    </el:formGroup>

</el:modal>
--}%

</el:formGroup>



<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     multiple="true"
                     class=" isRequired"
                     controller="province"
                     action="autocomplete"
                     name="provinceFirmsListId"
                     label="${message(code: 'firm.provinceFirms.label', default: 'provinceFirms')}"
                     values="${[[firm?.provinceFirms?.province?.id, firm?.provinceFirms?.province?.descriptionInfo?.localName]]}"
                     id="provinceFirms"/>
</el:formGroup>

<el:formGroup>
    <el:textArea name="note" size="8" class="" label="${message(code: 'firm.note.label', default: 'note')}"
                 value="${firm?.note}"/>
</el:formGroup>

