
const constraints = {
    audio: true,
    video: true
}


const joinButton = document.getElementById('join')
const joinButtonAudio = document.getElementById('joinAudio')
const join = async ()=>{
    $('#body').preloader()
    let conference=null
    try {
        conference = await VoxeetSDK.conference.fetch(document.getElementById('conferenceId').value)
    }
    catch(e) {
        $('#body').preloader('remove')
        $('#joinError').css('display','block')
    }
    if(!conference){

            $('#body').preloader('remove')
            $('#joinError').css('display','block')
        VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
    }
    else
    {
    VoxeetSDK.conference.join(conference,{constraints}).then(()=>{
            $('#body').preloader('remove')
            document.getElementById('video-super-container').style.display='flex'

    }  ).catch(e=>{
            $('#body').preloader('remove')
            $('#joinError').css('display','block')
            VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
            VoxeetSDK.conference.leave()
            console.log('error',e)
        })
    }
}
const joinAudio = async ()=>{
    $('#body').preloader()
    let conference=null
    try {
        conference = await VoxeetSDK.conference.fetch(document.getElementById('conferenceId').value)
    }
    catch(e) {
        $('#body').preloader('remove')
        $('#joinError').css('display','block')
    }
    if(!conference){

        $('#joinError').css('display','block')
        $('#body').preloader('remove')
        VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
    }
    else
    {
        VoxeetSDK.conference.join(conference,{audio:true,video:false}).then(()=>{
            interval()
            $('#body').preloader('remove')
            document.getElementById('audioComponent').style.display='flex'
        }  ).catch(e=>{
            $('#joinError').css('display','block')
            $('#body').preloader('remove')
            console.log('error',e)
        })
    }
}
joinButton.addEventListener('click',join)
joinButtonAudio.addEventListener('click',joinAudio)