$("#chatBtn").click(()=>{
    $("#chatMessenger").css('display','flex')
    $("#messengerBtn").css('display','none')
})
$("#chatClose").click(()=>{
    $("#messengerBtn").css('display','flex')
    $("#chatMessenger").css('display','none')
})