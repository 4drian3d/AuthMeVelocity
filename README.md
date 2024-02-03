# AuthMeVelocity
[![WorkFlow Status](https://img.shields.io/github/actions/workflow/status/4drian3d/AuthMeVelocity/gradle.yml?branch=master&style=flat-square)](https://github.com/4drian3d/AuthmeVelocity/actions/workflows/gradle.yml)
[![Version](https://img.shields.io/github/v/release/4drian3d/AuthmeVelocity?color=FFF0&style=flat-square)](https://modrinth.com/plugin/authmevelocity)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&label=Discord)](https://discord.gg/5NMMzK5mAn)

This plugin adds the support for [Velocity](https://velocitypowered.com/) to [AuthMeReloaded](https://github.com/AuthMe/AuthMeReloaded)

## Requirements
- Paper or Folia 1.20+
- Velocity 3.3.0+
- Java 17+

## Features
- Prevent your players from executing commands or typing in the chat before they are logged in
- Forces the first server that players enter to be an Auth server
- Send players to another server when logging in natively (AuthMeReloaded has bugs with this functionality on its own)
- Prevents players from having to re-login each time they join a server with AuthMe installed
- Get access to the AuthMe API from Velocity
- Compatibility with [FastLogin](https://github.com/games647/FastLogin) (AutoLogin support in the proxy) and [MiniPlaceholders](https://modrinth.com/plugin/miniplaceholders) (use AuthMeVelocity placeholders in any other plugin and vice versa)

## Setup
1. Download the latest release of the plugin [link](https://modrinth.com/plugin/authmevelocity)
2. Install AuthMeVelocity-Proxy on your Velocity Proxy
3. Install AuthMeVelocity-Paper on your Paper servers that have AuthMeReloaded installed
4. Start the Velocity proxy and set up the config.conf with the auth servers.

## Plugin API
Check the plugin API [here](https://github.com/4drian3d/AuthMeVelocity/wiki/Plugin-API)

### Javadocs
- Paper API [Javadocs](https://javadoc.io/doc/io.github.4drian3d/authmevelocity-api-paper)
- Velocity API [Javadocs](https://javadoc.io/doc/io.github.4drian3d/authmevelocity-api-velocity)

## Configuration
Check the plugin configuration [here](https://github.com/4drian3d/AuthMeVelocity/wiki/Configuration)

## Metrics
[![metrics](https://bstats.org/signatures/velocity/AuthMeVelocity.svg)](https://bstats.org/plugin/velocity/AuthMeVelocity/16128)
