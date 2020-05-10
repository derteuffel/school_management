document.getElementById('video-super-container').style.display = "none"
const addVideoNode = (participant, stream) => {
    const videoContainer = document.getElementById('video-container');
    let videoNode = document.getElementById('video-' + participant.id);

    if(!videoNode) {
        videoNode = document.createElement('video');

        videoNode.setAttribute('id', 'video-' + participant.id);
        videoNode.setAttribute('width', '100%');
        videoNode.setAttribute('class','col-md-6 p-1');
        videoNode.style.borderRadius="16px"
        videoContainer.appendChild(videoNode);

        videoNode.autoplay = 'autoplay';
        videoNode.muted = true;
    }

    navigator.attachMediaStream(videoNode, stream);
}
const stopVideoBtn = document.getElementById("stop")
stopVideoBtn.onclick = () => {
    const videoContainer = document.getElementById('video-container')
    VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
        .then(() => {
            document.getElementById('video-super-container').style.display = "none"
        })
    VoxeetSDK.conference.leave()
        .then(() => {
            document.getElementById('video-super-container').style.display = "none"
        })
        .catch((err) => {
            document.getElementById('video-super-container').style.display = "none"
            console.log(err);
        })
    VoxeetSDK.session.close()
}