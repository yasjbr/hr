
<g:render template="/DescriptionInfo/wrapper" model="[bean:correspondenceTemplate?.descriptionInfo]" />

<el:formGroup>
    <el:textAreaDescription name="coverLetter" size="8"
                  class="" label="${message(code:'correspondenceTemplate.coverLetter.label',default:'coverLetter')}"
                  value="${correspondenceTemplate?.coverLetter}"/>
</el:formGroup>