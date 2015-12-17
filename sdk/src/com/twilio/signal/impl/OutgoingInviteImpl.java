package com.twilio.signal.impl;

import android.os.Handler;

import com.twilio.signal.Conversation;
import com.twilio.signal.ConversationCallback;
import com.twilio.signal.OutgoingInvite;
import com.twilio.signal.InviteStatus;
import com.twilio.signal.impl.logging.Logger;
import com.twilio.signal.impl.util.CallbackHandler;

import java.util.Set;

public class OutgoingInviteImpl implements OutgoingInvite {

	static final Logger logger = Logger.getLogger(OutgoingInviteImpl.class);
	private final Handler handler;

	private ConversationsClientImpl conversationsClientImpl;
	private ConversationImpl conversation;
	private ConversationCallback conversationCallback;
	private InviteStatus inviteStatus;

	private OutgoingInviteImpl(ConversationsClientImpl conversationsClientImpl,
							   ConversationImpl conversation,
							   ConversationCallback conversationCallback) {
		this.conversation = conversation;
		this.conversationsClientImpl = conversationsClientImpl;
		this.conversationCallback = conversationCallback;
		this.inviteStatus = InviteStatus.PENDING;
		this.handler = CallbackHandler.create();
		if(handler == null) {
			throw new IllegalThreadStateException("This thread must be able to obtain a Looper");
		}
	}

	static OutgoingInviteImpl create(ConversationsClientImpl conversationsClientImpl,
								 ConversationImpl conversationImpl,
								 ConversationCallback conversationCallback) {
		if(conversationsClientImpl == null) {
			return null;
		}
		if(conversationImpl == null) {
			return null;
		}
		if(conversationCallback == null) {
			return null;
		}
		return new OutgoingInviteImpl(conversationsClientImpl, conversationImpl, conversationCallback);
	}

	Handler getHandler() {
		return handler;
	}

	void setStatus(InviteStatus inviteStatus) {
		this.inviteStatus = inviteStatus;
	}

	Conversation getConversation() {
		return conversation;
	}

	ConversationCallback getConversationCallback() {
		return conversationCallback;
	}

	@Override
	public void cancel() {
		if(inviteStatus == InviteStatus.PENDING) {
			logger.i("Cancelling pending invite");
			inviteStatus = InviteStatus.CANCELLED;
			conversation.disconnect();
		} else {
			logger.w("The invite was not cancelled. Invites can only be cancelled in the pending state");
		}
	}

	@Override
	public Set<String> getInvitedParticipants() {
		return conversation.getParticipants(); 
	}

	@Override
	public InviteStatus getStatus() {
		return inviteStatus;
	}

}
