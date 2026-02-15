FROM gitpod/workspace-full-vnc:latest

SHELL ["/bin/bash", "-c"]

# Set Environment Variables
ENV ANDROID_HOME=$HOME/Android/Sdk
ENV PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

USER root

# 1. Install Dependencies & Updated Tailscale
RUN curl -fsSL https://pkgs.tailscale.com/stable/ubuntu/focal.noarmor.gpg | tee /usr/share/keyrings/tailscale-archive-keyring.gpg >/dev/null \
    && curl -fsSL https://pkgs.tailscale.com/stable/ubuntu/focal.tailscale-keyring.list | tee /etc/apt/sources.list.d/tailscale.list \
    && apt-get update \
    && apt-get install -y tailscale libgtk-3-dev libnss3-dev fonts-noto fonts-noto-cjk

# 2. Use JDK 17 (Required for Panda cycle IDE runtime)
RUN install-packages openjdk-17-jdk -y \
    && update-java-alternatives --set java-1.17.0-openjdk-amd64

# 3. VNC Performance Tuning
RUN sed -i 's|resize=scale|resize=remote|g' /opt/novnc/index.html

USER gitpod

# 4. Install Android Studio Panda 2 (2025.3.2 Canary 4)
# Updated download link for the Feb 2026 Canary release
RUN cd $HOME && \
    wget https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2025.3.2.4/android-studio-2025.3.2.4-linux.tar.gz && \
    tar -zxvf android-studio-2025.3.2.4-linux.tar.gz && \
    rm android-studio-2025.3.2.4-linux.tar.gz

RUN mkdir -p $ANDROID_HOME
