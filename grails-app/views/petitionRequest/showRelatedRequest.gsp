<el:modal isModalWithDiv="true"  id="petitionRequestModal" title="${message(code:'petitionRequest.label')}" preventCloseOutSide="true" width="80%">
    <g:render template="show" model="[petitionRequest:petitionRequest]" />
</el:modal>