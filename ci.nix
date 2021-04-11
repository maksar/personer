builtins.mapAttrs (k: _v:
  let
    fl = (builtins.getFlake "https://github.com/maksar/personer.git").defaultPackage.${pkgs.system};
    url = "https://github.com/NixOS/nixpkgs/archive/2255f292063ccbe184ff8f9b35ce475c04d5ae69.tar.gz";
    pkgs = import (builtins.fetchTarball url) { system = k; };
  in
  pkgs.recurseIntoAttrs {
    # These two attributes will appear in your job for each platform.
    fl = fl;
    hello = pkgs.hello;
    cow-hello = pkgs.runCommand "cow-hello" {
      buildInputs = [ pkgs.hello pkgs.cowsay ];
    } ''
      hello | cowsay > $out
    '';
  }
) {
  # x86_64-linux = {};
  # Uncomment to test build on macOS too
  x86_64-darwin = {};
}